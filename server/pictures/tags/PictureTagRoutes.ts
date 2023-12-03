import {Express} from "express";
import {ServePictureTags} from "./ServePictureTags.js";
import {ManageJwtTokens} from "../../security/ManageJwtTokens.js";
import {IncorrectEmployeeException} from "../../users/IncorrectEmployeeException.js";
import {PictureNotFoundException} from "../PictureNotFoundException.js";

export default function(app: Express, pictureTagService: ServePictureTags, manageJwtTokens: ManageJwtTokens) {
    app.post("/api/pictures/tags", async (req, res) => {
        const token = req.get('authorization');
        if (!token) {
            res.sendStatus(400);
            return;
        }

        const authenticatedUser = await manageJwtTokens.decodeToken(token);
        if (!authenticatedUser || !authenticatedUser.email) {
            res.sendStatus(401);
            return;
        }

        try {
            const pictureTag = await pictureTagService.addTag(req.body.pictureId, req.body.tag, authenticatedUser);
            res.status(200).send(pictureTag);
        } catch (e) {
            if (e instanceof IncorrectEmployeeException) {
                res.sendStatus(403);
                return;
            }

            if (e instanceof PictureNotFoundException) {
                res.sendStatus(400);
                return;
            }

            res.status(500).send("Something bad happened here :|");
        }
    });

    app.delete("/api/pictures/:pictureId/tags/:tagId", async (req, res) => {
        const token = req.get('authorization');
        if (!token) {
            res.sendStatus(400);
            return;
        }

        const authenticatedUser = await manageJwtTokens.decodeToken(token);
        if (!authenticatedUser || !authenticatedUser.email) {
            res.sendStatus(401);
            return;
        }

        try {
            const pictureIdString = req.params.pictureId;
            const pictureId = Number(pictureIdString);

            const tagIdString = req.params.tagId;
            const tagId = Number(tagIdString);

            await pictureTagService.deleteTag(pictureId, tagId, authenticatedUser);
            res.sendStatus(200);
        } catch (e) {
            if (e instanceof IncorrectEmployeeException) {
                res.sendStatus(403);
                return;
            }

            res.status(500).send("Something bad happened here :|");
        }
    });
}