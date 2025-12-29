import uuid
from pydantic import BaseModel, Field


class ActivityModel(BaseModel):
    activity_id: uuid.UUID
    name: str
    description: str
    tourist_spot_id: uuid.UUID


class ActivityCreateUpdateModel(BaseModel):
    name: str = Field(max_length=80)
    description: str
    tourist_spot_id: uuid.UUID
