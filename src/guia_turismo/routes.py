from fastapi import APIRouter, status, Depends
from fastapi.exceptions import HTTPException
from .schemas import TourGuideCreateModel, TourGuideModel, TourGuideUpdateModel
from .service import TourGuideService
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.main import get_session


tour_router = APIRouter()
tourguide_service = TourGuideService()

@tour_router.post("", response_model=TourGuideCreateModel, status_code=status.HTTP_201_CREATED)
async def create_tour_guide_account(
    tour_guide_data: TourGuideCreateModel, 
    session: AsyncSession = Depends(get_session)
):
    new_tour_guide_account = await tourguide_service.create_tourguide_user(tour_guide_data, session)

    return new_tour_guide_account


@tour_router.get("/{tourguide_id}", response_model=TourGuideModel, status_code=status.HTTP_200_OK)
async def get_tour_guide_details(
    tourguide_id: str,
    session: AsyncSession = Depends(get_session)
):
    get_tourguide = await tourguide_service.get_tour_guide(tourguide_id, session)
    if get_tourguide is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Guia de turismo não encontrado"
        )

    return get_tourguide


@tour_router.put("/{tourguide_id}", response_model=TourGuideModel, status_code=status.HTTP_200_OK)
async def update_tourguide_data(
    tourguide_id: str,
    tourguide_updated_data: TourGuideUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    get_tourguide = await tourguide_service.get_tour_guide(tourguide_id, session)
    if get_tourguide is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Guia de turismo não encontrado"
        )

    tourguide_updated = await tourguide_service.update_tourguide_data(tourguide_id, tourguide_updated_data, session)

    return tourguide_updated


@tour_router.delete(path="/{tourguide_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_tourguide(tourguide_id: str, session: AsyncSession = Depends(get_session)):
    deleted_tourguide = await tourguide_service.delete_guide(tourguide_id, session)

    if deleted_tourguide is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="City not found"
        )

    return {}
