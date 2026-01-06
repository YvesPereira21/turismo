from fastapi import APIRouter, Depends, status
from fastapi.exceptions import HTTPException
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.main import get_session
from src.tags.schemas import TagCreateUpdateListModel, TagModel
from src.tags.service import TagService
from src.autenticacao.dependencies import RoleChecker


tag_router = APIRouter()
tag_service = TagService()
admin_role = Depends(RoleChecker(['admin']))

@tag_router.post("", response_model=TagModel, dependencies=[admin_role])
async def create_tag(
    tag_data: TagCreateUpdateListModel, session: AsyncSession = Depends(get_session)
):
    tag_name = tag_data.name
    tag_exists = await tag_service.tag_exists(tag_name, session)

    if tag_exists:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Tag already registered"
        )

    new_tag = await tag_service.create_tag(tag_data, session)

    return new_tag
