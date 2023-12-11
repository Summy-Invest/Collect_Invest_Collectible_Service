# Collectibles Service API

## Описание
API для управления коллекционными предметами.

## Запросы

### 1. Покупка коллекционного предмета

#### Метод: `POST`
#### Путь: `/buyCollectible`

Пример запроса:
```json
{
  "collectibleId": 1,
  "userId": 123,
  "sharesToBuy": 5
}
```

Пример ответа (в случае успеха):
```json
{
  "status": "OK",
  "message": "Покупка успешно выполнена"
}
```

Пример ответа в случае ошибки:
```json
{
"status": "BadRequest",
"message": "Error while processing buy request"
}
```

### 2. Продажа коллекционного предмета

#### Метод: `POST`
#### Путь: `/sellCollectible`

Пример запроса:
```json
{
  "collectibleId": 1,
  "userId": 123,
  "sharesToSell": 5
}
```

Пример ответа (в случае успеха):
```json
{
  "status": "OK",
  "message": "Продажа успешно выполнена"
}
```

Пример ответа в случае ошибки:
```json
{
  "status": "InternalServerError",
  "message": "Error while processing sell request"
}
```


### 3. Получение коллекционного предмета по ID

#### Метод: `GET`
#### Путь: `/getCollectibleById/{collectibleId}`

Пример запроса:
```json
GET /getCollectibleById/1
```

Пример ответа (в случае успеха):
```json
{
  "id": 1,
  "name": "Название предмета",
  "description": "Описание предмета",
  "category": "Категория",
  "photoLink": "Ссылка на фото",
  "currentPrice": 10.5,
  "availableShares": 95
}
```

Пример ответа в случае ошибки:
```json
{
  "status": "NotFound",
  "message": "Error while processing getCollectibleById request"
}
```

### 4. Получение всех коллекционных предметов

#### Метод: `GET`
#### Путь: `/getAllCollectibles`

Пример запроса:
```json
GET /getAllCollectibles
```

Пример ответа (в случае успеха):
```json
[
  {
    "id": 1,
    "name": "Название предмета 1",
    "description": "Описание предмета 1",
    "category": "Категория 1",
    "photoLink": "Ссылка на фото 1",
    "currentPrice": 10.5,
    "availableShares": 95
  },
  {
    "id": 2,
    "name": "Название предмета 2",
    "description": "Описание предмета 2",
    "category": "Категория 2",
    "photoLink": "Ссылка на фото 2",
    "currentPrice": 15.0,
    "availableShares": 80
  }
]
```

Пример ответа в случае ошибки:
```json
{
  "status": "InternalServerError",
  "message": "Error while processing getAllCollectibles request"
}
```
