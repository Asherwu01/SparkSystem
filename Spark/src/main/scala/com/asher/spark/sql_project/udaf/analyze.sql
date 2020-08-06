CREATE TABLE `user_visit_action`(
  `date` string,
  `user_id` bigint,
  `session_id` string,
  `page_id` bigint,
  `action_time` string,
  `search_keyword` string,
  `click_category_id` bigint,
  `click_product_id` bigint,
  `order_category_ids` string,
  `order_product_ids` string,
  `pay_category_ids` string,
  `pay_product_ids` string,
  `city_id` bigint
)


CREATE TABLE `product_info`(
    `product_id` bigint,
    `product_name` string,
    `extend_info` string
)

CREATE TABLE `city_info`(
    `city_id` bigint,
    `city_name` string,
    `area` string
)


