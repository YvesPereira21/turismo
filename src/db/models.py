import uuid
import sqlalchemy.dialects.postgresql as pg
from typing import List, Optional, Any
from sqlmodel import SQLModel, Field, Column, String, Relationship
from datetime import datetime, time, date
from geoalchemy2 import Geography


class User(SQLModel, table=True):
    __tablename__ = "users_tb" # type: ignore

    user_id: uuid.UUID = Field(
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            default_factory=uuid.uuid4,
            info={"description": "Unique identifier for the user account"},
        )
    )
    username: str = Field(exclude=True)
    name: str
    email: str
    password: str = Field(exclude=True)
    localization: Any = Field(sa_column=Column(Geography("POINT", srid=4326)))
    tour_guide: Optional["TourGuide"] = Relationship(
        sa_relationship_kwargs={"uselist": False},
        back_populates="user"
    )

    model_config = {"arbitrary_types_allowed": True} # type: ignore


class TourGuide(SQLModel, table=True):
    __tablename__="tourist_guide_db" # type: ignore

    guide_id: uuid.UUID = Field(
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            default_factory=uuid.uuid4,
            info={"description": "Unique identifier for the Tour Guide"}
        )
    )
    number_phone: int
    user_id: uuid.UUID = Field(foreign_key="users_tb.user_id")
    user: User = Field(foreign_key="users_tb.user_id", ondelete="CASCADE")
    tourist_spot: List["TouristSpot"] = Relationship(back_populates="tour_guide")


class SpotTags(SQLModel, table=True):
    __tablename__ = "spot_tags" # type: ignore

    spot_id: uuid.UUID = Field(foreign_key="tourist_spot_db.spot_id", primary_key=True)
    tag_id: uuid.UUID = Field(foreign_key="tag_db.tag_id", primary_key=True)


class TouristSpot(SQLModel, table=True):
    __tablename__ = "tourist_spot_db" # type: ignore

    spot_id: uuid.UUID = Field(
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            default_factory=uuid.uuid4,
            info={"description": "Unique identifier for the spot"}
        )
    )
    name: str
    localization: Any = Field(sa_column=Column(Geography("POINT", srid=4326)))
    time_open: time = Field(sa_column=Column(pg.TIME))
    time_close: time = Field(sa_column=Column(pg.TIME))
    description: str
    tour_guide_id: uuid.UUID = Field(default=None, foreign_key="tourist_guide_db.guide_id", ondelete="SET NULL")
    tour_guide: TourGuide = Relationship(
        back_populates="tourist_spot", 
        sa_relationship_kwargs={"lazy": "joined"}
    )
    city_id: uuid.UUID = Field(default=None, foreign_key="city_db.city_id", ondelete="CASCADE")
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

    model_config = {"arbitrary_types_allowed": True} # type: ignore


class Tag(SQLModel, table=True):
    __tablename__ = "tag_db" # type: ignore

    tag_id: uuid.UUID = Field(
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            default_factory=uuid.uuid4,
            info={"description": "Unique identifier for the tag"}
        )
    )
    name: str
    tourists_spots: List["TouristSpot"] = Relationship(
        link_model=SpotTags,
        back_populates="tags"
    )


class City(SQLModel, table=True):
    __tablename__ = "city_db" # type: ignore

    city_id: uuid.UUID = Field(
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            default_factory=uuid.uuid4,
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
    __tablename__ = "event_db" # type: ignore

    event_id: uuid.UUID = Field(
        sa_column=Column(
            pg.UUID(as_uuid=True),
            primary_key=True,
            unique=True,
            nullable=False,
            default_factory=uuid.uuid4,
            info={"description": "Unique identifier for the event"}
        )
    )
    description: str
    date_event: date = Field(sa_column=Column(pg.DATE))
    time_start: time = Field(sa_column=Column(pg.TIME))
    time_end: time = Field(sa_column=Column(pg.TIME))
    tourist_spot_id: uuid.UUID = Field(foreign_key="tourist_spot_db.spot_id", ondelete="CASCADE")
    tourist_spot: TouristSpot = Relationship(
        back_populates="events",
        sa_relationship_kwargs={"lazy": "joined"}
    )
