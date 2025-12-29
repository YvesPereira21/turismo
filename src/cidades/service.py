from fastapi import status
from fastapi.exceptions import HTTPException
from src.db.models import City, TouristSpot
from .schemas import CityCreateUpdateModel, CityListTouristsSpots
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession


class CityService:
    async def create_city(self, city_data: CityCreateUpdateModel, session: AsyncSession):
        city_data_dict = city_data.model_dump()
        new_city = City(**city_data_dict)

        session.add(new_city)
        await session.commit()

        return new_city


    async def return_city(self, city_name: str, session: AsyncSession):
        statement = select(City).where(City.name == city_name)
        result = await session.exec(statement)

        city = result.first()
        if city is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="City with this name not found"
            )

        return city


    async def update_city(
            self, city_name: str, city_updated_data: CityCreateUpdateModel, session: AsyncSession
    ):
        city = await self.return_city(city_name, session)

        city_updated = city_updated_data.model_dump()
        for k, v in city_updated.items():
            setattr(city, k, v)

        await session.commit()

        return city


    async def delete_city(self, city_name: str, session: AsyncSession):
        city_to_delete = await self.return_city(city_name, session)

        await session.delete(city_to_delete)
        await session.commit()

        return {}


    async def list_all_tourists_spots(self, city_name: str, session: AsyncSession):
        statement = select(City).where(City.name == city_name)
        result = await session.exec(statement)
        
        return result.first()
