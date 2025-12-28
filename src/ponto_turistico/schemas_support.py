import uuid
from datetime import time
from pydantic import BaseModel
from typing import List
from src.db.models import TourGuide
from src.tags.schemas import TagCreateUpdateListModel


class TouristSpotListModel(BaseModel):
    spot_id: uuid.UUID
    name: str
    time_open: time
    time_close: time
    description: str
    tour_guide: TourGuide
    tags: List[TagCreateUpdateListModel]
