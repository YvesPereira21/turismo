from fastapi import status
from fastapi.exceptions import HTTPException
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession
from geoalchemy2 import WKTElement
from src.db.models import TouristSpot, TourGuide, City, Tag, SpotTags
from .schemas import TouristSpotCreateModel, TouristSpotUpdateModel, TouristSpotCreateActivities


class TouristSpotService:
    async def create_tourist_spot(self, touristspot_data: TouristSpotCreateModel, session: AsyncSession):
        touristspot_data_dict = touristspot_data.model_dump()

        longitude = touristspot_data_dict.pop("longitude")
        latitude = touristspot_data_dict.pop("latitude")

        tourguide_id = touristspot_data_dict.pop("tourguide_id")
        city_name = touristspot_data_dict.pop("city_name")
        tag_name = touristspot_data_dict.pop("tag_name")

        localization = create_wktelement(longitude, latitude)
        tour_guide = await self.get_tourguide(tourguide_id, session)
        city = await self.get_city(city_name, session)
        tag = await self.get_tag(tag_name, session)

        new_touristspot = TouristSpot(**touristspot_data_dict)
        new_touristspot.localization = localization
        new_touristspot.tour_guide = tour_guide
        new_touristspot.city = city
        new_touristspot.tags.append(tag)

        session.add(new_touristspot)
        await session.commit()
        await session.refresh(new_touristspot)

        return new_touristspot


    async def get_touristspot(self, touristspot_id: str, session: AsyncSession):
        statement = select(TouristSpot).where(TouristSpot.spot_id == touristspot_id)

        result = await session.exec(statement)
        tourist_spot = result.first()

        return tourist_spot


    async def update_touristspot(self, touristspot_id: str, touristspot_updated: TouristSpotUpdateModel, session: AsyncSession):
        tourist_spot = await self.get_touristspot(touristspot_id, session)

        tourist_spot_dict = touristspot_updated.model_dump()
        for k, v in tourist_spot_dict.items():
            setattr(tourist_spot, k, v)

        await session.commit()

        return tourist_spot


    async def delete_touristspot(self, touristspot_id: str, session: AsyncSession):
        tourist_spot = await self.get_touristspot(touristspot_id, session)

        await session.delete(tourist_spot)
        await session.commit()

        return {}


    async def get_tourguide(self, tourguide_id: str, session: AsyncSession):
        statement = select(TourGuide).where(TourGuide.guide_id == tourguide_id)

        result = await session.exec(statement)
        tourguide = result.first()
    
        if tourguide is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Tag {tourguide_id} not found"
            )

        return tourguide


    async def get_city(self, name: str, session: AsyncSession):
        statement = select(City).where(City.name == name)

        result = await session.exec(statement)
        city = result.first()

        if city is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"City {name} not found"
            )

        return city


    async def get_tag(self, name: str, session: AsyncSession):
        # statement = select(Tag).join(SpotTags).where(Tag.name == name)
        statement = select(Tag).join(SpotTags).where(Tag.name == name)

        result = await session.exec(statement)
        tag = result.first()

        if tag is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Tag {name} not found"
            )

        return tag


def create_wktelement(longitude: float, latitude: float) -> WKTElement:
    return WKTElement(f"POINT({longitude} {latitude})", srid=4326)
