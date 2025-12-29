import uuid
from datetime import time
from pydantic import BaseModel


class TouristSpotListModel(BaseModel):
    spot_id: uuid.UUID
    name: str
    time_open: time
    time_close: time
    description: str
