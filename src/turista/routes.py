from fastapi import APIRouter, Depends, status
from fastapi.exceptions import HTTPException
from src.db.main import get_session
from sqlmodel.ext.asyncio.session import AsyncSession
from .schemas import TouristModel, TouristCreateModel, TouristUpdateModel, TouristCurrentLocalizationModel
from .service import TouristService


tourist_router = APIRouter()
tourist_service = TouristService()

@tourist_router.post("/signup", response_model=TouristModel, status_code=status.HTTP_201_CREATED)
async def create_account(
    tourist_data: TouristCreateModel,
    session: AsyncSession = Depends(get_session)
):
    tourist_account = await tourist_service.create_tourist_account(tourist_data, session)

    return tourist_account


@tourist_router.get("/{tourist_id}", response_model=TouristModel, status_code=status.HTTP_200_OK)
async def get_tourist_detail(
    tourist_id: str,
    session: AsyncSession = Depends(get_session)
):
    tourist = await tourist_service.get_tourist(tourist_id, session)
    if tourist is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Tourist not found"
        )

    return tourist


@tourist_router.put("/{tourist_id}", response_model=TouristModel, status_code=status.HTTP_200_OK)
async def update_tourist_data(
    tourist_id: str,
    tourist_data: TouristUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    tourist_updated = await tourist_service.update_tourist_account(tourist_id, tourist_data, session)

    return tourist_updated


@tourist_router.delete("/{tourist_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_tourist_account(
    tourist_id: str,
    session: AsyncSession = Depends(get_session)
):
    tourist_deleted = await tourist_service.delete_tourist(tourist_id, session)

    return {}


@tourist_router.patch("/{tourist_id}", response_model=TouristModel, status_code=status.HTTP_200_OK)
async def set_current_location(
    tourist_id: str,
    location_data: TouristCurrentLocalizationModel,
    session: AsyncSession = Depends(get_session)
):
    tourist_location = await tourist_service.update_tourist_location(tourist_id, location_data, session)
    if tourist_location is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Tourist not found"
        )

    return tourist_location
