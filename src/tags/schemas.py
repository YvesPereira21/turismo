import uuid
from typing import List
from pydantic import BaseModel, Field


class TagModel(BaseModel):
    tag_id: uuid.UUID
    name: str


class TagCreateUpdateListModel(BaseModel):
    name: str
