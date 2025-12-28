import uuid
from typing import List
from pydantic import BaseModel, Field
from src.usuario.schemas import AccountCreateModel


class TourGuideModel(BaseModel):
    guide_id: uuid.UUID
    phone: str
    cadastur: str
    user_id: uuid.UUID
    user: AccountCreateModel


class TourGuideCreateModel(BaseModel):
    number_phone: int
    user: AccountCreateModel
    spot_id: uuid.UUID = Field(exclude=True)


class TourGuideUpdateModel(BaseModel):
    number_phone: int
    cadastur: str


class TourGuideAddTouristSpotModel(BaseModel):
    spot_id: uuid.UUID = Field(exclude=True)


class TourguideModel(BaseModel):
    tourist_id: uuid.UUID
    name: str
    email: str
