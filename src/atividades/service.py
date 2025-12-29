from fastapi import status
from fastapi.exceptions import HTTPException
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.models import Activity, TouristSpot
from .schemas import ActivityCreateUpdateModel


class ActiviyService:
    async def create_activity(self, activity_data: ActivityCreateUpdateModel, session: AsyncSession):
        activity_data_dict = activity_data.model_dump()
        touristspot_id = activity_data_dict.pop('tourist_spot_id')

        tourist_spot = await self.get_tourist_spot(touristspot_id, session)
        new_activity = Activity(**activity_data_dict)
        new_activity.tourist_spot = tourist_spot

        session.add(new_activity)
        await session.commit()

        return new_activity


    async def get_activity(self, activity_id: str, session: AsyncSession):
        statement = select(Activity).where(Activity.activity_id == activity_id)

        result = await session.exec(statement)
        activity = result.first()

        if activity is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Activity not found"
            )

        return activity


    async def update_activity(self, activity_id: str, activity_data_updated: ActivityCreateUpdateModel, session: AsyncSession):
        activity = await self.get_activity(activity_id, session)

        activity_updated_dict = activity_data_updated.model_dump()
        for k, v in activity_updated_dict.items():
            setattr(activity, k, v)

        await session.commit()

        return activity


    async def delete_activity(self, activity_id: str, session: AsyncSession):
        activity = await self.get_activity(activity_id, session)

        await session.delete(activity)
        await session.commit()

        return {}


    async def get_tourist_spot(self, touristspot_id: str, session: AsyncSession):
        statement = select(TouristSpot).where(TouristSpot.spot_id == touristspot_id)

        result = await session.exec(statement)
        tourist_spot = result.first()

        if tourist_spot is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Tourist Spot not found"
            )
        
        return tourist_spot
