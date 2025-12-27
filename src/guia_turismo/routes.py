from fastapi import APIRouter, status, Depends
from fastapi.exceptions import HTTPException
from .schemas import TourGuideCreateModel, TourGuideModel, TourGuideUpdateModel, TourGuideTouristsSpotsListModel
from .service import TourGuideService
from sqlmodel.ext.asyncio.session import AsyncSession
from src.db.main import get_session


tour_router = APIRouter()
tour_guide = TourGuideService()

@tour_router.post("/", response_model=TourGuideCreateModel, status_code=status.HTTP_201_CREATED)
async def create_tour_guide_account(
    tour_guide_data: TourGuideCreateModel, 
    session: AsyncSession = Depends(get_session)
):
    pass