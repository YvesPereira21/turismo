from datetime import datetime, timedelta
from fastapi import APIRouter, Depends, status
from fastapi.responses import JSONResponse
from fastapi.exceptions import HTTPException
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.main import get_session
from .service import UserService
from .schemas import UserLoginModel, UserModel, ProfileResponse
from .utils import create_access_token, verify_password, decode_token
from .dependencies import RoleChecker, AccessTokenBearer, RefreshTokenBearer, get_current_user


auth_router = APIRouter()
auth_service = UserService()
admin_role = RoleChecker(['admin'])
tourguide_role = RoleChecker(['tourguide'])
tourist_role = RoleChecker(['tourist'])

REFRESH_TIME = 7

auth_router.post('/login', status_code=status.HTTP_200_OK)
async def login_user(login_data: UserLoginModel, session: AsyncSession = Depends(get_session)):
    email = login_data.email
    password = login_data.password

    user = await auth_service.get_user_by_email(email, session)
    if user is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuário não existe"
        )

    password_valid = verify_password(password, user.password)
    if not password_valid:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Informações incorretas"
        )

    access_token = create_access_token(
        user_data={
            'user_id': str(user.user_id),
            'email': user.email,
            'role': user.role
        }
    )

    refresh_token = create_access_token(
        user_data={
            'user_id': str(user.user_id),
            'email': user.email,
            'role': user.role
        },
        expire=timedelta(days=REFRESH_TIME),
        refresh=True
    )

    return JSONResponse(
        status_code=status.HTTP_200_OK,
        content={
            "access_token": access_token,
            "refresh_token": refresh_token,
            "user": {
                "email": user.email,
                "uid": str(user.user_id)
            }
        }
    )


@auth_router.get("/refresh_token")
async def get_new_access_token(token_details: dict = Depends(RefreshTokenBearer())):
    expire_timestamp = token_details['exp']

    if datetime.fromtimestamp(expire_timestamp) < datetime.now():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invalid token"
        )
    
    new_access_token = create_access_token(token_details['user'])

    return JSONResponse(content={'access_token': new_access_token})


@auth_router.get("/me", response_model=ProfileResponse)
async def get_current_user(current_user=Depends(get_current_user), _ : bool = Depends(admin_role)):
    response = ProfileResponse(
        id=current_user.user_id,
        email=current_user.email,
    )

    if current_user.role == "tourist" and current_user.tourist_profile:
        response.name = current_user.tourist_profile.name
    elif current_user.role == "tourguide" and current_user.guide_profile:
        response.name = current_user.guide_profile.name
        response.phone = current_user.guide_profile.phone
        response.cadastur = current_user.guide_profile.cadastur
        
    return response
