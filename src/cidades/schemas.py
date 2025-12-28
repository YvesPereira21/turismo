import uuid
from pydantic import BaseModel
from typing import List
from src.ponto_turistico.schemas_support import TouristSpotListModel


class CityModel(BaseModel):
    city_id: uuid.UUID
    name: str
    zip_code: str
    state: str


class CityCreateUpdateModel(BaseModel):
    name: str
    zip_code: str


class CityListTouristsSpots(BaseModel):
    name: str
    tourists_spots: List[TouristSpotListModel]
