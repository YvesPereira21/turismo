import uuid
from typing import List
from pydantic import BaseModel, Field
from src.usuario.schemas import UserCreateModel
from src.ponto_turistico.schemas import TourGuideTouristsSpotsListModel


class TourGuideModel(BaseModel):
    guide_id: uuid.UUID
    number_phone: int
    user_id: uuid.UUID
    user: UserCreateModel
    tourist_spot: List[TourGuideTouristsSpotsListModel]


class TourGuideCreateModel(BaseModel):
    number_phone: int
    user: UserCreateModel
    spot_id: uuid.UUID = Field(exclude=True)


class TourGuideUpdateModel(BaseModel):
    number_phone: int
    spot_id: uuid.UUID = Field(exclude=True)
