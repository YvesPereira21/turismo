import uuid
import jwt
import logging
from datetime import datetime, timedelta, timezone
from src.config import Config
from passlib.context import CryptContext


password_context = CryptContext(schemes=['bcrypt'])

ACESS_TOKEN_EXPIRE = 3600

def generate_password_hash(password: str) -> str:
    hash = password_context.hash(password)

    return hash


def verify_password(password: str, hash: str) -> bool:
    return password_context.verify(password, hash)


def create_access_token(user_data: dict, expire: timedelta = None, refresh: bool = False):
    payload = {}

    payload['user'] = user_data
    payload['exp'] = datetime.now(timezone.utc) + (expire if expire is not None else timedelta(seconds=ACESS_TOKEN_EXPIRE))
    payload['jti'] = str(uuid.uuid4())
    payload['refresh'] = refresh

    token = jwt.encode(
        payload=payload,
        key=Config.JWT_SECRET,
        algorithm=Config.JWT_ALGORITHM
    )

    return token


def decode_token(token: str) -> dict:
    try:
        token_decoded = jwt.decode(
            jwt=token,
            key=Config.JWT_SECRET,
            algorithms=[Config.JWT_ALGORITHM]
        )

        return token_decoded
    except jwt.PyJWTError as error:
        logging.error(error)

        return {}
