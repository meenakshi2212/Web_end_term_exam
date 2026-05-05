export type TagType = {
  id: number;
  name: string;
};

export type UserType = {
  id: number;
  username: string;
  displayName: string;
  avatarUrl?: string;
};

export type NoteType = {
  id: number;
  title: string;
  content: string;
  favorite: boolean;
  updatedAt: string;
  tags: TagType[];
  owner?: UserType;
};

export type CommentType = {
  id: number;
  text: string;
  createdAt: string;
  author: UserType;
};
