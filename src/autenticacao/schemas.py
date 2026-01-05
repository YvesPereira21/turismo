import uuid
from typing import Any, Optional
from pydantic import BaseModel, Field


class UserModel(BaseModel):
    user_id: uuid.UUID
    email: str
    password: str = Field(exclude=True)


class UserLoginModel(BaseModel):
    email: str
    password: str = Field(exclude=True)


class ProfileResponse(BaseModel):
    id: uuid.UUID
    email: str
    name: Optional[str] = None
    phone: Optional[str] = None
    cadastur: Optional[str] = None
