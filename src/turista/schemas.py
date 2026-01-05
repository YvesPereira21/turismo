import uuid
from pydantic import BaseModel
from src.autenticacao.schemas import AccountCreateModel


class TouristModel(BaseModel):
    tourist_id: uuid.UUID
    name: str
    email: str


class TouristCreateModel(BaseModel):
    name: str
    user: AccountCreateModel


class TouristUpdateModel(BaseModel):
    name: str
