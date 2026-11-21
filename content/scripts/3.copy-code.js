async function onPageClick(event) {
  const button = event.target.closest(".code-block__copy");
  if (!button || button.classList.contains("code-block__copy--copied")) {
    return;
  }

  const codeElement = button.closest(".code-block")?.querySelector("pre code");
  if (!codeElement) {
    return;
  }
  await navigator.clipboard.writeText(codeElement.innerText);

  button.classList.add("code-block__copy--copied");
  setTimeout(() => {
    button.classList.remove("code-block__copy--copied");
  }, 1500);
}

document.addEventListener("click", onPageClick)
