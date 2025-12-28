import uuid
from pydantic import BaseModel, Field


class TagModel(BaseModel):
    tag_id: uuid.UUID
    name: str


class TagCreateUpdateListModel(BaseModel):
    name: str = Field(min_length=3, max_length=40)
