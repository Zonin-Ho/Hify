# Hify 智能问答系统

## 简介
Hify 是一个基于langchain4j的智能问答系统，它能够回答用户的问题，并且能够根据用户的问题，生成相应的答案。

## 相关技术
**后端：**
- langchain4j
- springboot3.4+
- mybatis
- mysql8.0
- minIO

**前端：**
- vue3
- ant-design-vue
- vite
## 运行
**后端：**
- ==JKD17==
- 导入数据库文件
- 修改配置文件
- 运行

**数据库sql脚本：**

```sql
create table if not exists conversations
(
    id         char(36)  default (uuid())          not null
        primary key,
    user_id    char(36)                            not null,
    model_id   char(36)                            null,
    title      varchar(255)                        null,
    created_at timestamp default CURRENT_TIMESTAMP null,
    updated_at timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint conversations_ibfk_1
        foreign key (user_id) references hify.users (id)
            on delete cascade,
    constraint conversations_ibfk_2
        foreign key (model_id) references hify.models (id)
            on delete set null
);

create index model_id
    on conversations (model_id);

create index user_id
    on conversations (user_id);

create table if not exists files
(
    id                char(36)   default (uuid())          not null
        primary key,
    knowledge_base_id char(36)                             not null,
    file_name         varchar(255)                         not null,
    file_type         varchar(50)                          null,
    file_path         varchar(512)                         not null,
    file_size         bigint                               null,
    is_processed      tinyint(1) default 0                 null,
    created_at        timestamp  default CURRENT_TIMESTAMP null,
    constraint files_ibfk_1
        foreign key (knowledge_base_id) references hify.knowledge_bases (id)
            on delete cascade
);

create index knowledge_base_id
    on files (knowledge_base_id);

create table if not exists knowledge_bases
(
    id                   char(36)  default (uuid())          not null
        primary key,
    user_id              char(36)                            not null,
    name                 varchar(255)                        not null,
    description          text                                null,
    chroma_collection_id varchar(255)                        null,
    created_at           timestamp default CURRENT_TIMESTAMP null,
    updated_at           timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint knowledge_bases_ibfk_1
        foreign key (user_id) references hify.users (id)
            on delete cascade
);

create index user_id
    on knowledge_bases (user_id);

create table if not exists login_logs
(
    id         char(36)  default (uuid())          not null
        primary key,
    user_id    char(36)                            not null,
    ip_address varchar(50)                         null,
    user_agent varchar(512)                        null,
    login_at   timestamp default CURRENT_TIMESTAMP null,
    constraint login_logs_ibfk_1
        foreign key (user_id) references hify.users (id)
            on delete cascade
);

create index user_id
    on login_logs (user_id);

create table if not exists messages
(
    id              char(36)  default (uuid())          not null
        primary key,
    conversation_id char(36)                            not null,
    role            enum ('user', 'bot')                not null,
    content         text                                not null,
    model_id        char(36)                            null,
    created_at      timestamp default CURRENT_TIMESTAMP null,
    constraint messages_ibfk_1
        foreign key (conversation_id) references hify.conversations (id)
            on delete cascade,
    constraint messages_ibfk_2
        foreign key (model_id) references hify.models (id)
            on delete set null
);

create index conversation_id
    on messages (conversation_id);

create index model_id
    on messages (model_id);

create table if not exists models
(
    id         varchar(255)                         not null
        primary key,
    provider   varchar(255)                         null,
    api_key    varchar(512)                         null,
    is_enabled tinyint(1) default 1                 null,
    created_at timestamp  default CURRENT_TIMESTAMP null
);

create table if not exists users
(
    id            char(36)  default (uuid())          not null
        primary key,
    username      varchar(255)                        not null,
    email         varchar(255)                        not null,
    password_hash varchar(32)                         not null,
    avatar_url    varchar(512)                        null,
    created_at    timestamp default CURRENT_TIMESTAMP null,
    updated_at    timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint email
        unique (email)
);


```

**前端：**
- 安装依赖(node > 18)
  > pnpm install
- 运行
  > pnpm run dev