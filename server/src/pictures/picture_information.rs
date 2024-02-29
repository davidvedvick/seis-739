use serde::Serialize;

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PictureInformation {
    pub id: i64,
    pub cat_employee_id: i64,
    pub file_name: String,
}
