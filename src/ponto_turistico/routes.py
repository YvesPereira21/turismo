from fastapi import APIRouter, status, Depends
from fastapi.exceptions import HTTPException
from src.db.main import get_session
from sqlmodel.ext.asyncio.session import AsyncSession
from .schemas import TouristSpotModel, TouristSpotCreateModel, TouristSpotUpdateModel, TouristSpotListNearbyModel
from .service import TouristSpotService
from src.autenticacao.dependencies import RoleChecker, AccessTokenBearer


touristspot_router = APIRouter()
touristspot_service = TouristSpotService()
access_token = Depends(AccessTokenBearer())
admin_tourguide_role = Depends(RoleChecker(['tourguide', 'admin']))
tourguide_role = Depends(RoleChecker(['tourguide']))
tourist_role = Depends(RoleChecker(['tourist']))

@touristspot_router.get("/nearbyspots", response_model=list[TouristSpotListNearbyModel], status_code=status.HTTP_200_OK, dependencies=[tourist_role])
async def calculate_distance_between_user_and_touristspot(
    radius: int,
    longitude: float,
    latitude: float,
    session: AsyncSession = Depends(get_session)
):
    distance = await touristspot_service.calculate_distance(radius, longitude, latitude, session)

    return distance


@touristspot_router.post("", response_model=TouristSpotModel, status_code=status.HTTP_201_CREATED, dependencies=[admin_tourguide_role])
async def create_touristspot(
    touristspot_data: TouristSpotCreateModel,
    session: AsyncSession = Depends(get_session)
):
    new_touristspot = await touristspot_service.create_tourist_spot(touristspot_data, session)
    if new_touristspot is None:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Provide the correct dates"
        )

    return new_touristspot


@touristspot_router.get("/{touristspot_id}", response_model=TouristSpotModel, status_code=status.HTTP_200_OK, dependencies=[access_token])
async def get_touristspot(
    touristspot_id: str,
    session: AsyncSession = Depends(get_session)
):
    tourist_spot = await touristspot_service.get_touristspot(touristspot_id, session)
    if tourist_spot is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Tourist Spout not found"
        )

    return tourist_spot


@touristspot_router.put("/{touristspot_id}", response_model=TouristSpotModel, status_code=status.HTTP_200_OK, dependencies=[admin_tourguide_role])
async def update_touristspot(
    touristspot_id: str,
    touristspot_updated_data: TouristSpotUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    touristspot_updated = await touristspot_service.update_touristspot(touristspot_id, touristspot_updated_data, session)
    if touristspot_updated is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Tourist Spout not found"
        )

    return touristspot_updated


@touristspot_router.delete("/{touristspot_id}", status_code=status.HTTP_204_NO_CONTENT, dependencies=[admin_tourguide_role])
async def delete_touristspot(
    touristspot_id: str,
    session: AsyncSession = Depends(get_session)
):
    touristspot_deleted = await touristspot_service.delete_touristspot(touristspot_id, session)

    return touristspot_deleted
