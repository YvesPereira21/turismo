from typing import Any, List, Optional
from fastapi import Request, Depends, status
from fastapi.exceptions import HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from .utils import decode_token
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.main import get_session
from .service import UserService
from src.db.models import User


user_service = UserService()

class TokenBearer(HTTPBearer):
    def __init__(self, auto_error=True):
        return super().__init__(auto_error=auto_error)


    async def __call__(self, request: Request) -> Optional[HTTPAuthorizationCredentials]:
        creds = await super().__call__(request)
        token = creds.credentials

        token_data = decode_token(token)

        return token_data


    def token_valid(self, token: str) -> bool:
        token_data = decode_token(token)

        return True if token_data is not None else False


    def verify_token_data(self, token_data):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Please override this method in child classes"
        )


class AccessTokenBearer(TokenBearer):
    def verify_access_token(self, token_data: dict) -> None:
        if not token_data:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Invalid token"
            )
        
        if token_data['refresh']:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Access token required"
            )


class RefreshTokenBearer(TokenBearer):
    def veriverify_access_token(self, token_data: dict) -> None:
        if not token_data:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Invalid token"
            )

        if not token_data['refresh']:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Refresh token required"
            )


async def get_current_user(
        token_data: dict = Depends(AccessTokenBearer),
        session: AsyncSession = Depends(get_session)
):
    user_email = token_data['user']['email']
    user = await user_service.get_user_by_email(user_email, session)

    return user


class RoleChecker:
    def __init__(self, allowed_roles: List[str]) -> None:
        self.allowed_roles = allowed_roles


    def __call__(self, current_user: User = Depends(get_current_user)) -> Any:
        if current_user.role in self.allowed_roles:
            return True

        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="You don't have access to this"
        )
