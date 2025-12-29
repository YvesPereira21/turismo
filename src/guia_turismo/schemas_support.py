from pydantic import BaseModel


class TourGuideReturnModel(BaseModel):
    name: str
    phone: str
