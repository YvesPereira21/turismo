import uuid
from pydantic import BaseModel, Field
from src.usuario.schemas import AccountCreateModel


class TourGuideModel(BaseModel):
    guide_id: uuid.UUID
    name: str
    phone: str
    cadastur: str
    email: str


class TourGuideCreateModel(BaseModel):
    name: str = Field(min_length=3, max_length=80)
    phone: str = Field(max_length=18)
    cadastur: str
    user: AccountCreateModel


class TourGuideUpdateModel(BaseModel):
    phone: str
    cadastur: str


class TourGuideAddTouristSpotModel(BaseModel):
    spot_id: uuid.UUID
