from fastapi import FastAPI
from src.usuario.routes import auth_router
from src.tags.routes import tag_router


version = "v1"

app = FastAPI(
    title="Terra Paraibana",
    description="Uma API REST que mapeia pontos turísticos do estado da Paraíba",
    version=version
)

app.include_router(auth_router, prefix=f"/api/{version}/user", tags=["user"])
app.include_router(tag_router, prefix=f"/api/{version}/tags", tags=["tags"])
