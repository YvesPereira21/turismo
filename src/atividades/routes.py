from fastapi import APIRouter, Depends, status
from src.db.main import get_session
from sqlmodel.ext.asyncio.session import AsyncSession
from .schemas import ActivityModel, ActivityCreateUpdateModel
from .service import ActiviyService


activity_route = APIRouter()
activity_service = ActiviyService()

@activity_route.post("", response_model=ActivityModel, status_code=status.HTTP_201_CREATED)
async def create_activity(
    activity_data: ActivityCreateUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    new_activity = await activity_service.create_activity(activity_data, session)

    return new_activity


@activity_route.get("/{activity_id}", response_model=ActivityModel, status_code=status.HTTP_200_OK)
async def get_activity(
    activity_id: str,
    session: AsyncSession = Depends(get_session)
):
    activity = await activity_service.get_activity(activity_id, session)

    return activity


@activity_route.put("/{activity_id}", response_model=ActivityModel, status_code=status.HTTP_200_OK)
async def update_activity(
    activity_id: str,
    activity_data: ActivityCreateUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    updated_activity = await activity_service.update_activity(activity_id, activity_data, session)

    return updated_activity


@activity_route.delete("/{activity_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_activity(
    activity_id: str,
    session: AsyncSession = Depends(get_session)
):
    activity = await activity_service.delete_activity(activity_id, session)

    return activity
