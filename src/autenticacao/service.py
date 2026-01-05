from src.db.models import User
from sqlmodel import select
from sqlalchemy.orm import selectinload
from sqlmodel.ext.asyncio.session import AsyncSession


class UserService:
    async def get_user_by_email(self, email: str, session: AsyncSession):
        statement = select(User).where(User.email == email).options(
            selectinload(User.tourist_profile),
            selectinload(User.guide_profile)
        )

        result = await session.exec(statement)
        user = result.first()

        return user


    async def user_exists(self, email: str, session: AsyncSession):
        user_exists = await self.get_user_by_email(email, session)

        return True if user_exists is not None else False
