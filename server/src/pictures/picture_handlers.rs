use std::sync::Arc;

use serde::Deserialize;
use warp::http::Response;
use warp::reject::custom;
use warp::reply::json;
use warp::{reject, Reply};

use crate::errors::Error::Unknown;
use crate::errors::RejectableResult;
use crate::pictures::picture_repository::PictureRepository;
use crate::pictures::picture_service::PictureService;
use crate::pictures::serve_pictures::{ServePictureFiles, ServePictureInformation};
use crate::users::cat_employee_repository::CatEmployeeRepository;

#[derive(Deserialize)]
pub struct PageQuery {
    page: Option<i32>,
    size: Option<i32>,
}

pub async fn get_pictures_handler(
    query: PageQuery,
    picture_service: Arc<PictureService<PictureRepository, CatEmployeeRepository>>,
) -> RejectableResult<impl Reply> {
    let pictures = match picture_service
        .get_picture_information(query.page, query.size)
        .await
    {
        Ok(p) => p,
        Err(e) => return Err(custom(Unknown)),
    };

    Ok(json(&pictures))
}

pub async fn get_picture_file_handler(
    id: i64,
    picture_service: Arc<PictureService<PictureRepository, CatEmployeeRepository>>,
) -> RejectableResult<impl Reply> {
    let option = picture_service.get_picture_file(id).await;

    match option {
        Ok(Some(p)) => Ok(Response::builder()
            .header("cache-control", "public, max-age=31536000, immutable")
            .body(p.file)),
        Ok(None) => Err(reject::not_found()),
        Err(e) => Err(custom(Unknown)),
    }
}