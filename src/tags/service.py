from src.db.models import Tag
from .schemas import TagCreateUpdateModel
from sqlmodel import select
from sqlmodel.ext.asyncio.session import AsyncSession


class TagService:
    async def create_tag(self, tag_data: TagCreateUpdateModel, session: AsyncSession):
        tag_data_dict = tag_data.model_dump()

        new_tag = Tag(**tag_data_dict)

        session.add(new_tag)
        await session.commit()

        return new_tag


    async def tag_exists(self, tag_name: str, session: AsyncSession):
        statement = select(Tag).where(Tag.name == tag_name)

        result = await session.exec(statement)
        tag = result.first()

        return True if tag is not None else False
