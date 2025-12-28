from fastapi import APIRouter, Depends, status
from fastapi.exceptions import HTTPException
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.main import get_session
from .service import CityService
from .schemas import CityCreateUpdateModel, CityListTouristsSpots, CityModel


#lembrar de lançar as exceções aqui
city_router = APIRouter()
city_service = CityService()

@city_router.post(path="", response_model=CityModel, status_code=status.HTTP_201_CREATED)
async def create_city(
    city_data: CityCreateUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    new_city = await city_service.create_city(city_data, session)

    return new_city


@city_router.get(path="/{city_name}", response_model=CityModel, status_code=status.HTTP_200_OK)
async def get_city(city_name: str, session: AsyncSession = Depends(get_session)):
    return await city_service.return_city(city_name, session)


@city_router.put(path="/{city_name}", response_model=CityModel, status_code=status.HTTP_200_OK)
async def update_city_data(
    city_name: str,
    city_data: CityCreateUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    get_city = await city_service.return_city(city_name, session)

    if get_city is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="City not found"
        )

    city_updated = city_service.update_city(city_name, city_data, session)

    return city_updated


@city_router.delete(path="/{city_name}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_city(city_name: str, session: AsyncSession = Depends(get_session)):
    deleted_city = await city_service.delete_city(city_name, session)

    if deleted_city is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="City not found"
        )

    return {}

@city_router.get(
    path="/{city_name}/tourists_spots", response_model=CityListTouristsSpots, status_code=status.HTTP_200_OK
)
async def get_all_tourists_spots_from_city(city_id: str, session: AsyncSession = Depends(get_session)):
    all_tourists_spots = await city_service.list_all_tourists_spots(city_id, session)

    if all_tourists_spots is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="City not found"
        )

    return all_tourists_spots
