# Changelog

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
