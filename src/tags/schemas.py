import uuid
from typing import List
from pydantic import BaseModel, Field
from src.db.models import TouristSpot


class TagModel(BaseModel):
    tag_id: uuid.UUID
    name: str


class TagCreateUpdateModel(BaseModel):
    name: str
