import { atom } from "recoil";

export const profileInfo = atom({
  key: "profileState",
  default: {
    'loggedIn':'false',
    'username': '',
    'jwt': '',
    'data': null,
  }
});