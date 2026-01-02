from typing import List
from fastapi import status
from fastapi.exceptions import HTTPException
from sqlmodel import col, select
from sqlmodel.ext.asyncio.session import AsyncSession
from geoalchemy2 import WKTElement
from geoalchemy2.functions import ST_Distance, ST_DWithin
from src.db.models import TouristSpot, TourGuide, City, Tag
from .schemas import TouristSpotCreateModel, TouristSpotUpdateModel


class TouristSpotService:
    async def create_tourist_spot(self, touristspot_data: TouristSpotCreateModel, session: AsyncSession):
        touristspot_data_dict = touristspot_data.model_dump()

        longitude = touristspot_data_dict.pop("longitude")
        latitude = touristspot_data_dict.pop("latitude")

        tourguide_id = touristspot_data_dict.pop("tourguide_id")
        city_name = touristspot_data_dict.pop("city_name")
        tags_name = touristspot_data_dict.pop("tags_name")

        localization = create_wktelement(longitude, latitude)
        tour_guide = await self.get_tourguide(tourguide_id, session)
        city = await self.get_city(city_name, session)
        tags = await self.get_tag(tags_name, session)

        new_touristspot = TouristSpot(**touristspot_data_dict)
        new_touristspot.localization = localization
        new_touristspot.tour_guide = tour_guide
        new_touristspot.city = city
        new_touristspot.tags = tags

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


    async def calculate_distance(self, radius: int, longitude: float, latitude: float, session: AsyncSession):
        user_localization = create_wktelement(longitude, latitude)
        radius_meter = radius * 1000

        statement = select(
            TouristSpot,
            ST_Distance(TouristSpot.localization, user_localization).label("distance")
        ).where(
            ST_DWithin(TouristSpot.localization, user_localization, radius_meter)
        ).order_by("distance")

        result = await session.exec(statement)
        results = result.all()

        tourists_spots = []
        for spot, dist in results:
            spot_with_dist = spot.model_dump()
            spot_with_dist["distance"] = f"{round(dist/1000, 1)} km"
            tourists_spots.append(spot_with_dist)

        return tourists_spots


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


    async def get_tag(self, tags: List[str], session: AsyncSession) -> list:
        statement = select(Tag).where(col(Tag.name).in_(tags))

        result = await session.exec(statement)
        tag = result.all()

        if tag is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Tags not found"
            )

        return list(tag)


def create_wktelement(longitude: float, latitude: float) -> WKTElement:
    return WKTElement(f"POINT({longitude} {latitude})", srid=4326)
