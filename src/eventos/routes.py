from fastapi import APIRouter, Depends, status
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.main import get_session
from .schemas import EventModel, EventCreateModel, EventUpdateModel
from .service import EventService


event_router = APIRouter()
event_service = EventService()

@event_router.post("", response_model=EventModel, status_code=status.HTTP_201_CREATED)
async def create_event(
    event_data: EventCreateModel,
    session: AsyncSession = Depends(get_session)
):
    new_event = await event_service.create_event(event_data, session)

    return new_event


@event_router.get("/{event_id}", response_model=EventModel, status_code=status.HTTP_200_OK)
async def get_event(
    event_id: str,
    session: AsyncSession = Depends(get_session)
):
    event = await event_service.get_event(event_id, session)

    return event


@event_router.put("/{event_id}", response_model=EventModel, status_code=status.HTTP_200_OK)
async def update_event(
    event_id: str,
    event_updated: EventUpdateModel,
    session: AsyncSession = Depends(get_session)
):
    event_updated_data = await event_service.update_event(event_id, event_updated, session)

    return event_updated_data


@event_router.delete("/{event_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_event(
    event_id: str,
    session: AsyncSession = Depends(get_session)
):
    event_deleted = await event_service.delete_event(event_id, session)

    return event_deleted
