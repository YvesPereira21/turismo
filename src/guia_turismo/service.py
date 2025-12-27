from fastapi import status
from src.db.models import TourGuide, TouristSpot, User
from src.usuario.schemas import UserCreateModel
from .schemas import TourGuideCreateModel
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession


class TourGuideService:
    async def create_tourguide_user(self, tour_guide_data: TourGuideCreateModel, session: AsyncSession):
        tour_guide_data_dict = tour_guide_data.model_dump()
        user = tour_guide_data_dict.pop("user")

        new_user = User(**user)
        new_user_tourguide = TourGuide(**tour_guide_data_dict)
        new_user_tourguide.user = new_user

        session.add(new_user_tourguide)
        await session.commit()

        return new_user_tourguide


    # async def oi(self):
    #     pass
