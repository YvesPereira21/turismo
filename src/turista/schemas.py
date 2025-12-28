import uuid
from geoalchemy2 import WKBElement, WKTElement
from geoalchemy2.shape import to_shape
from shapely.geometry import mapping
from pydantic import BaseModel, field_serializer
from typing import Any, Optional
from src.usuario.schemas import AccountCreateModel


class TouristModel(BaseModel):
    tourist_id: uuid.UUID
    name: str
    email: str
    localization: Optional[Any]

    @field_serializer("localization")
    def serialize_localization(self, value: Any, _info):

        if isinstance(value, WKBElement):

            shape = to_shape(value)

            return mapping(shape)

        return value


class TouristCreateModel(BaseModel):
    name: str
    user: AccountCreateModel


class TouristUpdateModel(BaseModel):
    name: str


class TouristCurrentLocalizationModel(BaseModel):
    longitude: float
    latitude: float
