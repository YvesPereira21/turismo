from fastapi import FastAPI


version = "v1"

app = FastAPI(
    title="Terra Paraibana",
    description="Uma API REST que mapeia pontos turísticos do estado da Paraíba",
    version=version
)