from pydantic import BaseModel
from typing import List


class CityGetModel(BaseModel):
    name: str
    zip_code: str
    state: str
