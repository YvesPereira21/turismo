import uuid
from datetime import time
from geoalchemy2 import WKBElement
from geoalchemy2.shape import to_shape
from shapely.geometry import mapping
from pydantic import BaseModel, Field, field_serializer
from typing import Any, List
from src.db.models import TourGuide, Activity
from src.cidades.schemas_support import CityGetModel
from src.tags.schemas import TagCreateUpdateListModel


class TouristSpotModel(BaseModel):
    spot_id: uuid.UUID = Field(exclude=True)
    name: str
    localization: Any
    time_open: time
    time_close: time
    description: str
    tour_guide: TourGuide
    city: CityGetModel
    tags: List[TagCreateUpdateListModel]
    activities: List[Activity] | None

    @field_serializer("localization")
    def serialize_location(self, value: Any, _info):
        if isinstance(value, WKBElement):
            return mapping(to_shape(value))
        return value


class TouristSpotCreateModel(BaseModel):
    name: str
    longitude: float
    latitude: float
    time_open: time
    time_close: time
    description: str
    tourguide_id: uuid.UUID
    city_name: str
    tags_name: List[str]


class TouristSpotUpdateModel(BaseModel):
    name: str
    time_open: time
    time_close: time
    description: str


class TouristSpotCreateActivities(BaseModel):
    activities: List[Activity]
