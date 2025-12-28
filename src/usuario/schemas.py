import uuid
from geoalchemy2 import WKBElement
from geoalchemy2.shape import to_shape
from shapely.geometry import mapping
from pydantic import BaseModel, Field, field_serializer
from typing import Any


class UserModel(BaseModel):
    user_id: uuid.UUID
    username: str = Field(exclude=True)
    name: str
    email: str
    password: str = Field(exclude=True)
    localization: Any

    @field_serializer("localization")
    def serialize_localization(self, value: Any, _info):
        if isinstance(value, WKBElement):

            shape = to_shape(value)

            return mapping(shape)
        
        return value


class UserCreateModel(BaseModel):
    username: str
    name: str = Field(max_length=80)
    email: str
    password: str = Field(min_length=8, max_length=16)
    latitude: float
    longitude: float


class AccountCreateModel(BaseModel):
    email: str
    password: str = Field(min_length=8, max_length=16)
