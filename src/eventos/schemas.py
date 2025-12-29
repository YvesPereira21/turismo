import uuid
from typing import Optional
from datetime import date, time
from pydantic import BaseModel, Field
from src.ponto_turistico.schemas_support import TouristSpotListModel


class EventModel(BaseModel):
    event_id: uuid.UUID
    description: str
    date_event: date
    time_start: time
    time_end: time
    tourist_spot: TouristSpotListModel


class EventCreateModel(BaseModel):
    description: str
    date_event: date
    time_start: time
    time_end: time
    tourist_spot_id: uuid.UUID


class EventUpdateModel(BaseModel):
    description: str
    date_event: date
    time_start: time
    time_end: time
