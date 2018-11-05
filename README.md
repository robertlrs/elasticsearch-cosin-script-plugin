# Emoji, flags and emoticons support for Elasticsearch

> Add support for emoji and flags in any Lucene compatible search engine!

If you wish to search `🍩` to find **donuts** in your documents, you came to the right place.

## [The `analysis-emoji` Plugin](/esplugin)

To index emoji, you need a custom Tokenizer which is not considering them as punctuation. You can either build an analyzer with the whitespace tokenizer [as described here](http://jolicode.com/blog/search-for-emoji-with-elasticsearch), or **use this plugin**.

The plugin expose a new `emoji_tokenizer`, based on `icu_tokenizer` but with custom BreakIterator rules to keep emoji!

[Head over the `/esplugin` directory for installation instructions](/esplugin).

## The Synonyms, flags and emoticons

Once you have a `🍩` token, you need to expand it to the token "donut", in **your language**. That's the goal of the [synonym dictionaries](/synonyms).

We build Solr / Lucene compatible synonyms files in all languages supported by [Unicode CLDR](http://cldr.unicode.org/) so you can set them up in an analyzer. It looks like this:

```
👩‍🚒 => 👩‍🚒, firefighter, firetruck, woman
👩‍✈ => 👩‍✈, pilot, plane, woman
🥓 => 🥓, bacon, meat, food
🥔 => 🥔, potato, vegetable, food
😅 => 😅, cold, face, open, smile, sweat
😆 => 😆, face, laugh, mouth, open, satisfied, smile
🚎 => 🚎, bus, tram, trolley
🇫🇷 => 🇫🇷, france
🇬🇧 => 🇬🇧, united kingdom
```

For emoticons, use [this mapping](../../java/emoji-search/emoticons.txt) with a char_filter to replace emoticons by emoji.

**Learn more about this in our [blog post describing how to search with emoji in Elasticsearch](http://jolicode.com/blog/search-for-emoji-with-elasticsearch) (2016).**

### Getting started

Download the emoji and emoticon file you want from this repository and store them in `PATH_ES/config/analysis`.

```
config
├── analysis
│   ├── cldr-emoji-annotation-synonyms-en.txt
│   └── emoticons.txt
├── elasticsearch.yml
...
```

Use them like this:

```json
PUT /en-emoji
{
  "settings": {
    "analysis": {
      "char_filter": {
        "emoticons_char_filter": {
          "type": "mapping",
          "mappings_path": "analysis/emoticons.txt"
        }
      },
      "filter": {
        "english_emoji": {
          "type": "synonym",
          "synonyms_path": "analysis/cldr-emoji-annotation-synonyms-en.txt" 
        }
      }
    }
  }
}
```

[Head over the `/esplugin` directory for a fully functional mapping](/esplugin).

## How to contribute

### Build from CLDR SVN

You will need:

- php cli
- php zip and curl extensions

Edit the tag in `tools/build-released.php` and run `php tools/build-released.php`.

### Update emoticons

Run `php tools/build-emoticon.php`.

## Licenses

Emoji data courtesy of CLDR. See [unicode-license.txt](../../java/emoji-search/unicode-license.txt) for details. Some modifications are done on the data, [see here](https://github.com/jolicode/emoji-search/issues/6).
Emoticon data based on [https://github.com/wooorm/emoticon/](https://github.com/wooorm/emoticon/) (MIT).

This repository in distributed under [MIT License](../../java/emoji-search/LICENSE). Feel free to use and contribute as you please!
