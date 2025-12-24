from src.db.models import User
from .schemas import UserCreateModel
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession
from geoalchemy2 import WKTElement


class UserService:
    async def get_user_by_email(self, email: str, session: AsyncSession):
        statement = select(User).where(User.email == email)

        result = await session.exec(statement)
        user = result.first()

        return user


    async def user_exists(self, email: str, session: AsyncSession):
        user_exists = await self.get_user_by_email(email, session)

        return True if user_exists is not None else False


    async def create_user(self, user_data: UserCreateModel, session: AsyncSession):
        user_data_dict = user_data.model_dump()

        latitude = user_data_dict.pop("latitude", None)
        longitude = user_data_dict.pop("longitude", None)

        new_user = User(**user_data_dict)
        new_user.role = "user"

        point_wkt = f"POINT({longitude} {latitude})"
        new_user.localization = WKTElement(point_wkt, srid=4326)

        session.add(new_user)
        await session.commit()

        await session.refresh(new_user)

        return new_user
