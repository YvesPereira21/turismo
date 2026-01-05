import uuid
import sqlalchemy.dialects.postgresql as pg
from typing import List, Optional, Any
from sqlmodel import SQLModel, Field, Column, Relationship
from datetime import datetime, time, date
from geoalchemy2 import Geography


class User(SQLModel, table=True):
    __tablename__ = "users_tb" # type: ignore

    user_id: uuid.UUID = Field(
        default_factory=uuid.uuid4,
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            info={"description": "Unique identifier for the user"}
        )
    )
    email: str = Field(unique=True, index=True)
    password: str
    role: str = Field(
        sa_column=Column(pg.VARCHAR, nullable=False, server_default="tourist")
    )
    tourist_profile: Optional["Tourist"] = Relationship(
        sa_relationship_kwargs={"uselist": False}, back_populates="user"
    )
    guide_profile: Optional["TourGuide"] = Relationship(
        sa_relationship_kwargs={"uselist": False}, back_populates="user"
    )


class Tourist(SQLModel, table=True):
    __tablename__ = "tourists_tb" # type: ignore

    tourist_id: uuid.UUID = Field(
        default_factory=uuid.uuid4,
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            info={"description": "Unique identifier for the tourist"}
        )
    )
    name: str
    user_id: uuid.UUID = Field(foreign_key="users_tb.user_id")
    user: User = Relationship(back_populates="tourist_profile", sa_relationship_kwargs={"lazy": "selectin"})

    @property
    def email(self) -> str:
        return self.user.email if self.user else ""

    @property
    def password(self) -> str:
        return self.user.password if self.user else ""

    model_config = {"arbitrary_types_allowed": True} # type: ignore


class TourGuide(SQLModel, table=True):
    __tablename__ = "tourist_guide_tb" # type: ignore
    
    guide_id: uuid.UUID = Field(
        default_factory=uuid.uuid4,
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            info={"description": "Unique identifier for the guide"}
        )
    )
    name: str
    phone: str
    cadastur: str
    user_id: uuid.UUID = Field(foreign_key="users_tb.user_id")
    user: User = Relationship(back_populates="guide_profile", sa_relationship_kwargs={"lazy": "selectin"})
    tourist_spot: Optional["TouristSpot"] = Relationship(
        back_populates="tour_guide", 
        sa_relationship_kwargs={"lazy": "selectin"}
    )

    @property
    def email(self) -> str:
        return self.user.email if self.user else ""

    @property
    def password(self) -> str:
        return self.user.password if self.user else ""


class SpotTags(SQLModel, table=True):
    __tablename__ = "spot_tags" # type: ignore

    spot_id: uuid.UUID = Field(foreign_key="tourist_spot_tb.spot_id", primary_key=True)
    tag_id: uuid.UUID = Field(foreign_key="tag_tb.tag_id", primary_key=True)


class TouristSpot(SQLModel, table=True):
    __tablename__ = "tourist_spot_tb" # type: ignore

    spot_id: uuid.UUID = Field(
        default_factory=uuid.uuid4,
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            info={"description": "Unique identifier for the spot"}
        )
    )
    name: str
    localization: Any = Field(sa_column=Column(Geography("POINT", srid=4326)))
    time_open: time = Field(sa_column=Column(pg.TIME))
    time_close: time = Field(sa_column=Column(pg.TIME))
    description: str
    tour_guide_id: Optional[uuid.UUID] = Field(
        default=None, 
        foreign_key="tourist_guide_tb.guide_id", 
        ondelete="SET NULL", 
        nullable=True
    )
    tour_guide: Optional[TourGuide] = Relationship(
        back_populates="tourist_spot", 
        sa_relationship_kwargs={"lazy": "joined"}
    )
    city_id: Optional[uuid.UUID] = Field(default=None, foreign_key="city_tb.city_id", ondelete="CASCADE")
    city: Optional["City"] = Relationship(
        back_populates="tourists_spots",
        sa_relationship_kwargs={"lazy": "joined"}
    )
    tags: List["Tag"] = Relationship(
        link_model=SpotTags,
        back_populates="tourists_spots",
        sa_relationship_kwargs={"lazy": "selectin"}
    )
    events: List["Event"] = Relationship(back_populates="tourist_spot", sa_relationship_kwargs={"lazy": "selectin"})
    activities: List["Activity"] = Relationship(back_populates="tourist_spot", sa_relationship_kwargs={"lazy": "selectin"})

    model_config = {"arbitrary_types_allowed": True} # type: ignore


class Tag(SQLModel, table=True):
    __tablename__ = "tag_tb" # type: ignore

    tag_id: uuid.UUID = Field(
        default_factory=uuid.uuid4,
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            info={"description": "Unique identifier for the tag"}
        )
    )
    name: str
    tourists_spots: List["TouristSpot"] = Relationship(
        link_model=SpotTags,
        back_populates="tags"
    )


class City(SQLModel, table=True):
    __tablename__ = "city_tb" # type: ignore

    city_id: uuid.UUID = Field(
        default_factory=uuid.uuid4,
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            info={"description": "Unique identifier for the city"}
        )
    )
    name: str
    zip_code: str
    state: str = Field(default="Paraíba")
    tourists_spots: List["TouristSpot"] = Relationship(
        back_populates="city",
        sa_relationship_kwargs={"lazy": "selectin"}
    )


class Event(SQLModel, table=True):
    __tablename__ = "event_tb" # type: ignore

    event_id: uuid.UUID = Field(
        default_factory=uuid.uuid4,
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            info={"description": "Unique identifier for the event"}
        )
    )
    description: str
    date_event: date = Field(sa_column=Column(pg.DATE))
    time_start: time = Field(sa_column=Column(pg.TIME))
    time_end: time = Field(sa_column=Column(pg.TIME))
    tourist_spot_id: uuid.UUID = Field(foreign_key="tourist_spot_tb.spot_id", ondelete="CASCADE")
    tourist_spot: TouristSpot = Relationship(
        back_populates="events",
        sa_relationship_kwargs={"lazy": "joined"}
    )


class Activity(SQLModel, table=True):
    __tablename__ = "activity_tb" # type: ignore

    activity_id: uuid.UUID = Field(
        default_factory=uuid.uuid4,
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            info={"description": "Unique identifier for the activity"}
        )
    )
    name: str
    description: str
    tourist_spot_id: uuid.UUID = Field(foreign_key="tourist_spot_tb.spot_id", ondelete="CASCADE")
    tourist_spot: TouristSpot = Relationship(
        back_populates="activities",
        sa_relationship_kwargs={"lazy": "joined"}
    )
