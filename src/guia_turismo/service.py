from fastapi import status
from src.db.models import TourGuide, User
from .schemas import TourGuideCreateModel, TourGuideUpdateModel
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession
from src.autenticacao.utils import generate_password_hash


class TourGuideService:
    async def create_tourguide_user(self, tour_guide_data: TourGuideCreateModel, session: AsyncSession):
        tourguide_data_dict = tour_guide_data.model_dump()
        user = tourguide_data_dict.pop("user")

        new_user = User(**user)
        new_user.password = generate_password_hash(user['password'])
        new_user.role = "tourguide"

        new_user_tourguide = TourGuide(**tourguide_data_dict)
        new_user_tourguide.user = new_user

        session.add(new_user_tourguide)

        await session.commit()
        await session.refresh(new_user_tourguide)

        return new_user_tourguide


    async def get_tour_guide(self, tour_guide_id: str, session: AsyncSession):
        statement = select(TourGuide).where(TourGuide.guide_id == tour_guide_id)

        result = await session.exec(statement)
        tour_guide = result.first()

        return tour_guide


    async def update_tourguide_data(
            self,
            tour_guide_id: str,
            tour_guide_updated: TourGuideUpdateModel, 
            session: AsyncSession
    ):
        get_tour_guide = await self.get_tour_guide(tour_guide_id, session)

        tour_guide_dict = tour_guide_updated.model_dump()
        for k, v in tour_guide_dict.items():
            setattr(get_tour_guide, k, v)

        await session.commit()

        return get_tour_guide


    async def delete_guide(self, tourguide_id: str, session: AsyncSession):
        tour_guide = await self.get_tour_guide(tourguide_id, session)

        await session.delete(tour_guide)
        await session.commit()

        return {}
