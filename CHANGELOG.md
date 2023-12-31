# Changelog

## 0.1.8

- [Fix error when `:static-paths` is provided without `:static-props`](https://github.com/rads/rain/commit/763e9124f086469b62dab4ee65878c49349c5bed)
- [Move static route transforms to `static-pages`](https://github.com/rads/rain/commit/2de4c13f80af515d84b5993d7769530964c48813)
- [Omit bootstrap data if `main-cljs-bundle-path` returns `nil`](https://github.com/rads/rain/commit/195eea303f5ba454f7de448cbcefeb99844e626c)
- [Fix #20: Hydration template](https://github.com/rads/rain/issues/20)

## 0.1.7

- [Update rain dep in SSG template](https://github.com/rads/rain/commit/154edd0320492ea796de3b194e5dd129b3bd07d1)

## 0.1.6

- [Fix #16: SSG template](https://github.com/rads/rain/issues/16)

## 0.1.5

- [Fix #7: Fill in docstrings](https://github.com/rads/rain/issues/7)
- [Fix #23: Remove `rain.re-frame/event` macro](https://github.com/rads/rain/issues/23)
- Removed `rain.core/href`
  - Use `rain.re-frame/href-alpha` instead
- Removed `rain.re-frame/event`
  - Use plain functions for event handlers instead
- Renamed `rain.re-frame/href` to `rain.re-frame/href-alpha`
  - Use this function temporarily until we implement `rain.re-frame/href` with an implicit `router` argument.

## 0.1.4

- [Fix #15: Add `rain` CLI](https://github.com/rads/rain/issues/15)
- [Fix #21: Support Form-2 components on server without helper function](https://github.com/rads/rain/issues/21)

## 0.1.3

This version includes changes necessary to implement the [SSG](https://github.com/rads/rain/issues/1) and [hydration](https://github.com/rads/rain/issues/5) examples.

**Examples:**

- SSG: [https://github.com/rads/rain.examples.ssg](https://github.com/rads/rain.examples.ssg) (fix [#1](https://github.com/rads/rain/issues/1))
- Hydration: [https://github.com/rads/rain.examples.todomvc](https://github.com/rads/rain.examples.todomvc) (fix [#5](https://github.com/rads/rain/issues/5))

**Changes:**

- Add `rain.re-frame/href`
- Fix set-page
- Update README
- Add `rain.re-frame/f`
- Update Usage
- Add `.html` suffixes (fix [#11](https://github.com/rads/rain/issues/11))
- Add `rain.core/href`
- Remove `.json` routes for now
- Remove `io.github.rads/xtrace` from `deps.edn`

## 0.1.2

- [Fix error when `:static-paths` is omitted (part 2)](https://github.com/rads/rain/commit/1434f93f75a41ad4440dea34676f22932ea36172)

## 0.1.1

- [Fix error when `:static-paths` is omitted](https://github.com/rads/rain/commit/f221928fc7f99e39f78541381817007b772c2e66)

## 0.1.0

- First release
