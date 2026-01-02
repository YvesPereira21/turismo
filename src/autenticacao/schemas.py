import uuid
from pydantic import BaseModel, Field


class UserModel(BaseModel):
    user_id: uuid.UUID
    email: str
    password: str = Field(exclude=True)


class UserLoginModel(BaseModel):
    email: str
    password: str = Field(exclude=True)
