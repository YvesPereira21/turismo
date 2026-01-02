from fastapi import FastAPI
from src.tags.routes import tag_router
from src.cidades.routes import city_router
from src.guia_turismo.routes import tour_router
from src.turista.routes import tourist_router
from src.ponto_turistico.routes import touristspot_router
from src.eventos.routes import event_router
from src.atividades.routes import activity_route


version = "v1"

app = FastAPI(
    title="Terra Paraibana",
    description="Uma API REST que mapeia pontos turísticos do estado da Paraíba",
    version=version
)

app.include_router(tag_router, prefix=f"/api/{version}/tags", tags=["tags"])
app.include_router(city_router, prefix=f"/api/{version}/cities", tags=["cities"])
app.include_router(tour_router, prefix=f"/api/{version}/tourguides", tags=["tourguides"])
app.include_router(tourist_router, prefix=f"/api/{version}/tourists", tags=["tourists"])
app.include_router(touristspot_router, prefix=f"/api/{version}/touristspots", tags=["touristspots"])
app.include_router(event_router, prefix=f"/api/{version}/events", tags=["events"])
app.include_router(activity_route, prefix=f"/api/{version}/activities", tags=["activities"])
