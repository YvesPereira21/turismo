from fastapi import status
from fastapi.exceptions import HTTPException
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.models import Event, TouristSpot
from .schemas import EventCreateModel, EventUpdateModel


class EventService:
    async def create_event(self, event_data: EventCreateModel, session: AsyncSession):
        event_data_dict = event_data.model_dump()

        tourist_spot_id = event_data_dict.pop('tourist_spot_id')
        tourist_spot = await self.get_touristspot(tourist_spot_id, session)

        new_event = Event(**event_data_dict)
        new_event.tourist_spot = tourist_spot

        session.add(new_event)
        await session.commit()

        return new_event


    async def get_event(self, event_id: str, session: AsyncSession):
        statement = select(Event).where(Event.event_id == event_id)
        result = await session.exec(statement)

        event = result.first()

        return event


    async def update_event(self, event_id: str, event_updated: EventUpdateModel, session: AsyncSession):
        event = await self.get_event(event_id, session)

        event_updated_dict = event_updated.model_dump()
        for k, v in event_updated_dict.items():
            setattr(event, k, v)

        return event


    async def delete_event(self, event_id: str, session: AsyncSession):
        event = await self.get_event(event_id, session)

        await session.delete(event)
        await session.commit()

        return {}


    async def get_touristspot(self, touristspot_id: str, session: AsyncSession):
        statement = select(TouristSpot).where(TouristSpot.spot_id == touristspot_id)
        result = await session.exec(statement)

        tourist_spot = result.first()

        if tourist_spot is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Tourist Spot not found"
            )

        return tourist_spot
