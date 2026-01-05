from fastapi import status
from fastapi.exceptions import HTTPException
from src.db.models import Tourist, User
from .schemas import TouristCreateModel, TouristUpdateModel
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession
from src.autenticacao.utils import generate_password_hash


class TouristService:
    async def create_tourist_account(
        self, tourist_data: TouristCreateModel, session: AsyncSession
    ):
        tourist_data_dict = tourist_data.model_dump()
        user_data = tourist_data_dict.pop('user')

        new_user = User(**user_data)
        new_user.password = generate_password_hash(user_data['password'])
        new_user.role = "tourist"

        new_tourist = Tourist(**tourist_data_dict)
        new_tourist.user = new_user

        session.add(new_tourist)
        await session.commit()
        await session.refresh(new_tourist)

        return new_tourist


    async def get_tourist(
        self, tourist_id: str, session: AsyncSession
    ):
        statement = select(Tourist).where(Tourist.tourist_id == tourist_id)

        result = await session.exec(statement)
        tourist = result.first()

        return tourist


    async def update_tourist_account(
        self, tourist_id: str, tourist_updated: TouristUpdateModel, session: AsyncSession
    ):
        tourist = await self.get_tourist(tourist_id, session)

        tourist_account_updated = tourist_updated.model_dump()
        for k, v in tourist_account_updated.items():
            setattr(tourist, k, v)

        await session.commit()

        return tourist


    async def delete_tourist(
        self, tourist_id: str, session: AsyncSession
    ):
        tourist = await self.get_tourist(tourist_id, session)

        await session.delete(tourist)
        await session.commit()

        return {}
