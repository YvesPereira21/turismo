from fastapi import APIRouter, Depends, status
from src.db.main import get_session
from sqlmodel.ext.asyncio.session import AsyncSession
from .schemas import ActivityModel, ActivityCreateUpdateModel
from .service import ActiviyService
from src.autenticacao.dependencies import RoleChecker, AccessTokenBearer


activity_route = APIRouter()
activity_service = ActiviyService()
access_token = Depends(AccessTokenBearer())
admin_role = Depends(RoleChecker(['admin']))
tourguide_role = Depends(RoleChecker(['tourguide']))

@activity_route.post("", response_model=ActivityModel, status_code=status.HTTP_201_CREATED, dependencies=[tourguide_role])
async def create_activity(
    activity_data: ActivityCreateUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    new_activity = await activity_service.create_activity(activity_data, session)

    return new_activity


@activity_route.get("/{activity_id}", response_model=ActivityModel, status_code=status.HTTP_200_OK, dependencies=[access_token])
async def get_activity(
    activity_id: str,
    session: AsyncSession = Depends(get_session)
):
    activity = await activity_service.get_activity(activity_id, session)

    return activity


@activity_route.put("/{activity_id}", response_model=ActivityModel, status_code=status.HTTP_200_OK, dependencies=[tourguide_role])
async def update_activity(
    activity_id: str,
    activity_data: ActivityCreateUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    updated_activity = await activity_service.update_activity(activity_id, activity_data, session)

    return updated_activity


@activity_route.delete("/{activity_id}", status_code=status.HTTP_204_NO_CONTENT, dependencies=[tourguide_role])
async def delete_activity(
    activity_id: str,
    session: AsyncSession = Depends(get_session)
):
    activity = await activity_service.delete_activity(activity_id, session)

    return activity
